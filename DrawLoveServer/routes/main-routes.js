var express = require('express');
var UserServices = require('../services/UserServices.js');

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
			if (err){
				res.status(500).json({reasonMessage: err});
			}
			else{
				req.session['currentUser'] = user;
				res.status(200).json({});
			}
		});
	});

router.route('/register')
	.post(function(req, res){
		var chatID = String(req.body['chatID']);
		var password = String(req.body['password']);
		var email = String(req.body['email']);
		console.log(req.body, chatID, password, email);
		UserServices.createNewUser(chatID, password, email, function(err, doc){
			console.log("save user returned");
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
		console.log("/checkChatID called: " + chatID);
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
