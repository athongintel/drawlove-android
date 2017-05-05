var express = require('express');

var GroupServices = require('../services/GroupServices.js');

var router = express.Router();

router.use(require('../middlewares/login-middleware.js'));

router.route('/')
	.get(function(req, res){
		//-- return created or joined groups
		GroupServices.getJoinedGroups(req.session['currentUser']._id, function(err, groups){
			if (!err){
				res.status(200).json({groups: groups});
			}
			else{
				res.status(500).json({});
			}
		});
	})
	.post(function(req, res){
		var groupName = req.body['name'];
		if (groupName){
			GroupServices.addGroup(req.session['currentUser']._id, groupName, function(err, group){
				if (!err && group){
					res.status(200).json(group);
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

router.route('/chat')
	.post(function(req, res){
		var groupID = req.body["groupID"];
		var messageCount = req.body["messageCount"];
		if (groupID){
			//-- check if group exits and the user belong to the group
			GroupServices.getGroupById(groupID, function(err, group){
				if (!err && group){
					console.log(group);
					if (group.members.indexOf(req.session["currentUser"]._id) >= 0){
						//-- get latest count message then return
						var result = {};
						result["group"] = group;
						GroupServices.getLatestMessages(groupID, messageCount, function(err, messages){
							if (!err && messages){
								result["messages"] = messages;
								res.status(200).json(result);
							}
							else{
								res.status(500).json({"reasonMessage": "Cannot load group messages"});
							}
						});
					}
					else{
						res.status(500).json({"reasonMessage" : "User not belong to the group"});
					}
				}
				else{
					res.status(500).json({"reasonMessage" : "Group not found."});
				}
			});
		}
		else{
			res.status(400).json({});
		}
	});

module.exports = router;