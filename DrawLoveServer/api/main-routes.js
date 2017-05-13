var express = require('express');
var UserServices = require('../services/UserServices.js');
var MailServices = require('../services/MailServices.js');

var router = express.Router();

router.route('/')
	.get(function(req, res){
		res.json({success: true, session: req.session});
	});

router.route('/login')
	.post(function(req, res){
		var chatID = String(req.body['chatID']);
		var password = String(req.body['password']);
		UserServices.authenticateUser(chatID, password, function(err, user){
			if (!err && user){
				req.session['currentUser'] = user;
				UserServices.activeUsers[user._id] = user;
				var result = {};
				result["user"] = user;
				UserServices.getAllFriends(user, function(err, friends){
					if (!err && friends){
						result["friends"] = friends;
						UserServices.getRequestSent(user._id, function(err, sentRequests){
							if (!err && sentRequests){
								result["sentRequests"] = sentRequests;
								UserServices.getRequestReceived(user._id, function(err, receivedRequests){
									if (!err && receivedRequests){
										result["receivedRequests"] = receivedRequests;
										res.status(200).json(result);
									}
									else{
										res.status(500).json({reasonMessage: err});
									}
								});
							}
							else{
								res.status(500).json({reasonMessage: err});
							}
						});
					}
					else{
						res.status(500).json({"reasonMessage" : err});
					}
				});
			}
			else{
				res.status(500).json({reasonMessage: err});
			}
		});
	});

router.route('/email_verification')
	.get(function(req, res){
		var token = req.query["token"];
		var userID = req.query["userID"];
		if (token && userID){
			//-- find user
			token = decodeURIComponent(token);
			UserServices.findUserById(userID, function(err, user){
				if (!err && user){
					if (MailServices.verifyEmail(user, token)){
						//-- success, update is activated
						user.isActivated = true;
						user.save(function(err, doc){
							if (!err && doc){
								res.status(200).json({status: "success"});
							}
							else{
								res.status(500).json({reasonMessage: err});
							}
						});
					}
					else{
						res.status(500).json({reasonMessage: "Cannot verify user. Invalid token."});
					}
				}
				else{
					res.status(500).json({reasonMessage: "Cannot find user."});
				}
			});
		}
		else{
			res.status(400).json({});
		}
	})

router.route('/logout')
	.get(function(req, res){
		var user = req.session['currentUser'];
		if (user){
			UserServices.activeUsers[user._id] = null;
			req.session['currentUser'] = null;
			//-- TODO: notify to this user's current active friends

			res.status(200).json({});
		}
		else{
			res.status(400).json({"reasonMessage" : "not logged in"})
		}
	});

router.route('/register')
	.post(function(req, res){
		var chatID = String(req.body['chatID']);
		var password = String(req.body['password']);
		var email = String(req.body['email']);
		UserServices.createNewUser(chatID, password, email, function(err, doc){
			if (err || !doc){
				res.status(500).json({reasonMessage: err.toString()})
			}
			else{
				res.status(200).json({});
			}
		});
	});

router.route('/checkChatID')
	.post(function(req, res){
		var chatID = String(req.body['chatID']);
		UserServices.checkChatID(chatID, function(err){
			if (err){
				res.status(500).json({reasonMessage: err});
			}
			else{
				res.status(200).json({});
			}
		});
	});

module.exports = router;
