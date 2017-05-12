var User = require('../models/User.js');
var Request = require('../models/Request.js');
var Group = require('../models/Group.js');
var randomstring = require('randomstring');
var crypto = require('crypto');
var MailServices = require('./MailServices.js');
var GroupServices = require('./GroupServices.js');

var UserServices = {

	activeUsers : {},

	findUserById: function(userID, cb){
		User.findById(userID, cb);
	},

	findUsersByIds: function(userIDs, cb){
		User.find({"_id" : {$in : userIDs}}, cb);
	},

	getUsersByIDs: function(currentUser, userIDs, cb){
		findUsersByIds(userIDs, function(err, users){
			if (!err && users){
				//-- TODO: filter information
				cb(null, users);
			}
			else{
				cb(err, null);
			}
		});
	},

	createNewUser : function(chatID, password, email, cb){
		//-- random salt
		var salt = randomstring.generate(8);
		//-- hash password
		var hash = crypto.createHash('sha256'); 
		hash.update(password);
		hash.update(salt);
		var passwordHash = hash.digest('base64');
		
		//-- create user
		var user = new User({"chatID" : chatID, "passwordHash": passwordHash, "salt": salt, "email": email});
		user.save(function(err, doc){
			if (!err && doc){
				//-- send email verification
				MailServices.sendVerificationEmail(doc, function(err, random){
					//-- if email cannot be sent then auto-active user account
					if (err){
						user.isActivated = true;
						user.save(cb);
					}
					else{
						user.emailToken = random;
						user.save(cb);
					}
				});
			}
			else{
				cb(err);
			}
		});
	},

	getAllFriends: function(user, cb){
		User.find({"_id" : {$in : user.friends}}).exec(cb);
	},

	requestFriend: function(currentUser, receiverID, cb){
		if (currentUser._id == receiverID){
			cb("Cannot make friend to yourself");
		}
		else{
			User.findById(receiverID, function(err, doc){
				if (!err && doc){
					//-- TODO : check states
					var request = new Request({"sender": currentUser._id, "receiver": doc._id, "type": "friend", "status": "pending", "requestData" : [currentUser.chatID, doc["chatID"]]});
					request.save(cb);
				}
				else{
					cb("Cannot find user");
				}
			});
		}
	},

	answerRequest: function(currentUser, requestID, status, cb){
		//-- check if currentUser is request's receiver
		Request.findById(requestID, function(err, request){
			if (!err && request){
				if (currentUser._id == request.receiver){
					if (request.status == "pending"){
						request.status = status;
						request.responseDate = new Date();
						request.save(function(err, doc){
							if (!err && doc){
								if (request.status == "accepted"){
									if (request.type == "friend"){
										//-- add friend
										User.findById(request.sender, function(err, sender){
											if (!err && sender){
												sender.friends.push(currentUser._id);
												sender.save(function(err, doc){
													if (!err && doc){
														User.findById(currentUser._id, function(err, d){
															if (!err && d){
																d.friends.push(request.sender);
																d.save(function(err, cur){
																	if (!err && cur){
																		cb(null, sender);
																	}
																	else{
																		cb(err, null);
																	}
																});
															}
															else{
																cb(err || "Cannot add friend right now.", null);
															}
														});
													}
													else{
														cb(err || "Cannot add friend right now.", null);
													}
												});
											}
											else{
												cb(err || "User not found", null);
											}
										});
									}
									else if (request.type == "group"){
										var groupID = request.requestData[0];
										Group.findById(groupID, function(err, group){
											if (!err && group){
												group.members.push(request.receiver);
												group.save(cb);
											}
											else{
												cb(err || "Group not found.", null);
											}
										});
									}
								}
								else{
									cb(null, doc);
								}
							}
							else{
								cb(err, null);
							}
						});
					}
					else{
						cb("Request had been processed before.");
					}
				}
				else{
					cb("User not own the request.");
				}
			}
			else{
				cb("Request not found.");
			}
		});
	},

	requestAddUserToGroup: function(currentUser, receiverID, groupID, cb){
		//-- check if receiverID is currentUser's friend
		if (currentUser.friends.indexOf(receiverID) >= 0){
			//-- check if user is in this group
			GroupServices.getGroupById(groupID, function(err, group){
				if (!err && group){
					if (group.members.indexOf(currentUser._id) >= 0){
						if (group.members.indexOf(receiverID) >= 0){
							//-- user is already in
							cb("User is already in this group");
						}
						else{
							//-- check if there is a pending or blocked request of this user to this group
							Request.find({"receiver": receiverID, "type": "group", "status": {$in : ["pending", "blocked"]}, "requestData": groupID}).exec(function(err, requests){
								if (!err){
									if (requests && requests.length){
										cb("User is already being invited");
									}
									else{
										User.findById(receiverID, function(err, user){
											if (!err && user){
												var request = new Request({"sender": currentUser._id, "receiver": user._id, "type": "group", "requestData": [groupID, group.name, currentUser.chatID, user.chatID]});
												request.save(cb);
											}
											else{
												cb("User not found");
											}
										});
									}
								}
								else{
									cb(err, null);
								}
							});
						}
					}
					else{
						cb("You do not have right to access this group.");
					}
				}
				else{
					cb("Group not found.");
				}
			});
		}
		else{
			cb("Can only add a friend to a group.");
		}
	},

	getRequestSent: function(userID, cb){
		Request.find({"sender": userID}).exec(cb);
	},

	getRequestReceived: function(userID, cb){
		Request.find({"receiver": userID}).exec(cb);
	},

	checkChatID : function(chatID, cb){
		User.findOne({"chatID": chatID}, function(err, user){
			if (user){
				cb("ChatID had been taken");
			}
			else{
				cb(null);
			}
		});
	},

	authenticateUser: function(chatID, password, cb){
		User.findOne({"chatID": chatID}, function(err, user){
			if (!err && user){
				if (user.isActivated){
					var hash = crypto.createHash('sha256');
					hash.update(password);
					hash.update(user.salt);
					var passwordHash = String(hash.digest('base64'));
					if (passwordHash == user.passwordHash){
						cb(null, user)
					}
					else
					{
						cb("Wrong password");
					}
				}
				else{
					cb("Account not activated");
				}
			}
			else{
				cb("ChatID not existed");
			}
		});
	},

	getAllByChatID: function(search, cb){
		User.find({chatID: {$regex: search}, isActivated: true}, cb);
	}

};

module.exports = UserServices;