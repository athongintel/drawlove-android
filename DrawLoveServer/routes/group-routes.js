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

module.exports = router;