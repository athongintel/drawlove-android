var express = require('express');
var UserServices = require('../services/UserServices.js');

var router = express.Router();

router.use(require('../middlewares/login-middleware.js'));

router.route('/')
	.get(function(req, res){
		//-- return currentUser
		res.status(200).json(req.session['currentUser']);
	});

router.route('/request_friend')
	.get(function(req, res){
		UserServices.getRequestFriend(userID, function(err, docs){
			if (!err && docs){
				res.status(200).json({requests: docs});
			}
			else{
				res.status(500).json({reasonMessage: err});
			}
		});
	})
	.post(function(req, res){
		//-- TODO: add new friend request
		var chatID = req.body['chatID'];
		if (chatID){
			UserServices.requestFriend(req.session['currentUser']._id, chatID, function(err){
				if (!err){
					res.status(200).json({});
				}
				else{
					res.status(500).json({reasonMessage: err});
				}
			});
		}
		else{
			res.status(400).json({});
		}
	});

router.route('/friend_request')
	.get(function(req, res){
		//-- get all friend requests received
		UserServices.getFriendRequest(req.session['currentUser']._id, function(err, docs){
			if (!err && docs){
				res.status(200).json({requests: docs});
			}
			else{
				res.status(500).json({reasonMessage: err});
			}
		});
	});

module.exports = router;