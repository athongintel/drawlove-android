var requireLogin = function(req, res, next){
	if (req.session['currentUser']){
		next();
	}
	else{
		res.status(400).json({reasonMessage: 'Session expired. Please login again.'});
	}
}

module.exports = requireLogin;