var User = require('../models/User.js');
var Request = require('../models/Request.js');
var randomstring = require('randomstring');
var crypto = require('crypto');

var UserServices = {

	activeUsers : {},

	createNewUser : function(chatID, password, email, cb){
		//-- random salt
		var salt = randomstring.generate(8);
		//-- hash password
		var hash = crypto.createHash('sha256'); 
		hash.update(password);
		hash.update(salt);
		var passwordHash = String(hash.digest('base64'));
		
		//-- create user
		var user = new User({chatID : chatID, passwordHash: passwordHash, salt: salt, email: email});
		user.save(function(err, doc){
			console.log("error: ", err);
			cb(err, doc);
		});
	},

	requestFriend: function(senderID, accepterChatID, cb){
		User.findOne({chatID: accepterChatID}, function(err, doc){
			if (!err && doc){
				var request = new Request({sender: senderID, accepter: doc._id, type: "friend", status: "pending"});
				request.save(cb);
			}
			else{
				cb("Cannot find user");
			}
		});
	},

	getRequestFriend: function(userID, cb){
		Request.find({sender: userID}).populate('sender').populate('accepter').exec(cb);
	},

	getFriendRequest: function(userID, cb){

	},

	checkChatID : function(chatID, cb){
		User.findOne({chatID: chatID}, function(err, user){
			if (user){
				cb("ChatID had been taken");
			}
			else{
				cb(null);
			}
		});
	},

	authenticateUser : function(chatID, password, cb){
		User.findOne({chatID: chatID}, function(err, user){
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
	}

};

module.exports = UserServices;