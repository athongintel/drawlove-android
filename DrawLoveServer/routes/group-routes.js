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
	});

module.exports = router;