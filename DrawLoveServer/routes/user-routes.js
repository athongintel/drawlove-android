var express = require('express');
var UserServices = require('../services/UserServices.js');

var router = express.Router();

router.use(require('../middlewares/login-middleware.js'));

router.route('/')
	.get(function(req, res){
		//-- return currentUser
		res.status(200).json(req.session['currentUser']);
	});

router.route('/search')
	.post(function(req, res){
		var search = req.body['search'];
		UserServices.getAllByChatID(search, function(err, docs){
			if (!err && docs){
				res.status(200).json({"users" : docs});
			}
			else{
				res.status(500).json({reasonMessage: err});
			}
		});
	});

router.route('/request/friend')
	.post(function(req, res){
		//-- TODO: add new friend request
		var userID = req.body['userID'];
		var requestType = "friend";
		if (userID){
			UserServices.requestFriend(req.session['currentUser']._id, userID, function(err, request){
				if (!err){
					res.status(200).json(request);
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

router.route('/request/all')
	.get(function(req, res){
		//-- get all friend requests received
		var result  = {};
		UserServices.getRequestFriend(req.session['currentUser']._id, function(err, requests){
			if (!err && requests){
				result["requests"] = requests;
				UserServices.getFriendRequest(req.session['currentUser']._id, function(err, friendRequests){
					if (!err && friendRequests){
						result["friendRequests"] = friendRequests;
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
	});

module.exports = router;