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

router.route('/request/add_to_group')
	.post(function(req, res){
		//-- add a user to a group
		var userID = req.body['userID'];
		var groupID = req.body['groupID'];
		if (userID){
			UserServices.requestAddUserToGroup(req.session['currentUser'], userID, groupID, function(err, request){
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

router.route('/request/action')
	.post(function(req, res){
		var requestID = req.body["requestID"];
		var status = req.body["status"];
		if (requestID && status){
			UserServices.answerRequest(req.session["currentUser"], requestID, status, function(err, request){
				if (!err && request){
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

router.route('/request/all')
	.get(function(req, res){
		//-- get all friends and requests received
		// var userIDs = [];
		var result  = {
			// "users": {}
		};
		UserServices.getRequestSent(req.session['currentUser']._id, function(err, sentRequests){
			if (!err && sentRequests){
				result["sentRequests"] = sentRequests;
				UserServices.getRequestReceived(req.session['currentUser']._id, function(err, receivedRequests){
					if (!err && receivedRequests){
						result["receivedRequests"] = receivedRequests;
						UserServices.findUsersByIds(req.session['currentUser'].friends, function(err, friends){
							if (!err && friends){
								result["friends"] = friends;
								//-- get userIDS
								// for (var i=0; i<sentRequests.length; i++){
								// 	userIDs.push(sentRequests[i].receiver._id);
								// }
								// for (var i=0; i<receivedRequests.length; i++){
								// 	userIDs.push(receivedRequests[i].sender._id);
								// }
								// UserServices.getUsersByIds(userIDs, function(err, docs){
								// 	if (!err && docs){
								// 		for (var i=0; i<docs.length; i++){
								// 			result.users[docs[i]._id] = docs[i];
								// 		}
										res.status(200).json(result);
								// 	}
								// 	else{
								// 		res.status(500).json({reasonMessage: err});
								// 	}
								// });
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
				res.status(500).json({reasonMessage: err});
			}
		});
	});

module.exports = router;