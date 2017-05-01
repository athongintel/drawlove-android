var express = require('express');
var UserServices = require('../services/UserServices.js');

var router = express.Router();

router.use(require('../middlewares/login-middleware.js'));

router.route('/')
	.get(function(req, res){
		//-- return currentUser
		res.json(req.session['currentUser']);
	});

module.exports = router;