var Group = require('../models/Group.js');
var Message = require('../models/Message.js');
var Request = require('../models/Request.js');

var GroupServices = {

	getGroupById: function(groupID, cb){
		Group.findById(groupID, cb);
	},

	getJoinedGroups: function(UserID, cb){
		Group.find({members: UserID}, function(err, groups){
			cb(err, groups);
		});
	},

	addGroup: function(creatorID, name, cb){
		var group = new Group({members: [creatorID], name: name, messages: []});
		group.save(cb);
	},

	getLatestMessages: function(groupID, count, cb){
		Message.find({'group': groupID}).sort({sentDate: 'desc'}).limit(count).exec(cb);
	},

	getAllRelatedRequests: function(groupID, cb){
		Request.find({"type": "group", "requestData": groupID}).exec(cb);
	}
}

module.exports = GroupServices;