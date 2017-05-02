var Group = require('../models/Group.js');

var GroupServices = {

	getJoinedGroups: function(UserID, cb){
		Group.find({members: UserID}, function(err, groups){
			cb(err, groups);
		});
	},

	addGroup: function(creatorID, name, cb){
		var group = new Group({members: [creatorID], name: name, messages: []});
		group.save(cb);
	}

}

module.exports = GroupServices;