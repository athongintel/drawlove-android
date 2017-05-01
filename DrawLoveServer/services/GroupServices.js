var Group = require('../models/Group.js');

var GroupServices = {

	getJoinedGroups: function(UserID, cb){
		Group.find({members: UserID}, function(err, groups){
			cb(err, groups);
		});
	}

}

module.exports = GroupServices;