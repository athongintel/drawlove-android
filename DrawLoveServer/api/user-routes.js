var express = require('express');
var UserServices = require('../services/UserServices.js');
var GroupServices = require('../services/GroupServices.js');

var router = express.Router();

router.use(require('../middlewares/login-middleware.js'));

router.route('/')
	.get(function(req, res){
		//-- return currentUser
		res.status(200).json(req.session['currentUser']);
	});

router.route('/get_users')
	.post(function(req, res){
		var userIDs = req.body["userIDs"];
		UserServices.getUsersByIDs(req.session['currentUser'], userIDs, function(err, users){
			if (!err & users){
				res.status(200).json({"users": users});
			}
			else{
				res.status(500).json({"reasonMessage": err});
			}
		});
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

router.route('/leave_group')
	.post(function(req, res){
		var groupID = req.body['groupID'];
		if (groupID){
			GroupServices.removeMember(req.session['currentUser']._id, groupID, function(err, group){
				if (!err && group){
					res.status(200).json({});
				}
				else{
					console.log(err);
					res.status(500).json({});
				}
			});
		}
		else{
			res.status(400).json({});
		}
	});

router.route('/request/friend')
	.post(function(req, res){
		//-- add new friend request
		var userID = req.body['userID'];
		if (userID){
			UserServices.requestFriend(req.session['currentUser'], userID, function(err, request){
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


router.route('/request/cancel')
	.post(function(req, res){
		var requestID = req.body['requestID'];
		if (!requestID){
			res.status(400).json({});
		}
		else{
			UserServices.cancelRequest(req.session['currentUser']._id, requestID, function(err, request){
				if (!err && request){
					res.status(200).json({});
				}
				else{
					res.status(400).json({"reason" : err});
				}
			});
		}
	});

router.route('/request/add_to_group')
	.post(function(req, res){
		//-- add a user to a group
		var userID = req.body['userID'];
		var groupID = req.body['groupID'];
		if (userID){
			UserServices.requestAddUserToGroup(req.session['currentUser'], userID, groupID, function(err, request){
				if (!err && request){
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

router.route('/request/action')
	.post(function(req, res){
		var requestID = req.body["requestID"];
		var status = req.body["status"];
		if (requestID && status){
			UserServices.answerRequest(req.session["currentUser"], requestID, status, function(err, result){
				if (!err && result){
					res.status(200).json(result);
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

module.exports = router;