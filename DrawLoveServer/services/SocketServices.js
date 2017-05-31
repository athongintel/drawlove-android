var UserServices = require('./UserServices.js');
var GroupServices = require('./GroupServices.js');
var Message = require('../models/Message.js');

const MESSAGE = 'message';
const DISCONNECT = 'disconnect';

var socketIO;
var sockets = {};

var SocketServices = {

	init: function(io){
		socketIO = io;

		io.on('connection', function(socket){
			socket.on(MESSAGE, function(obj){
				//-- check all logged in user
				// console.log(obj);
				var events = obj['event'].split(":");
				var data = JSON.parse(obj['data']);
				switch (true){
					case events[0] == "login":{
							var user = UserServices.activeUsers[data.userID];
							if (user && (user.ioSocketToken == data.ioSocketToken)){
								//-- socket OK, send response
								socket['user'] = user;
								socket.emit(MESSAGE, "login", {status : "success"});
								sockets[data.userID] = socket;
							}
							else{
								socket.emit(MESSAGE, "login", {status: "failed"});
							}
						}
						break;

					case events[0] == "request":{

						}
						break;

					case events[0] == "chat":{
							var groupID = data['group'];
							//-- check if user belong to group
							// console.log(data);
							GroupServices.getGroupById(groupID, function(err, group){
								if (!err && group){
									if (group.members.indexOf(socket['user']._id) >= 0){
										//-- create new message and save
										var message = new Message();
										message['contentType'] = data['contentType'];
										message['content'] = data['content'];
										message['group'] = groupID;
										message['sender'] = socket['user']._id;

										message.save(function(err, message){
											if (!err && message){
												//-- broad cast the message to people in the same group
												group.members.some(function(userID){
													if (String(userID) != String(socket['user']._id)){
														//-- check if user is live
														if (sockets[userID]){
															sockets[userID].emit(MESSAGE, obj['event'], message);
														}
													}
													else{
														//-- send message ACK
														obj.data = {'contentType' : -1, '_id': message._id, 'timestamp' : data['timestamp']};
														socket.emit(MESSAGE, obj['event'], obj.data);
														// console.log(obj.data);
													}
												});
											}
											else{
												//-- do nothing
											}
										});
										
									}
									else{
										//-- do nothing		
									}
								}
								else{
									//-- do nothing
								}
							});
						}
						break;

					default:
						break;
				}
			});
		});
	}
}

module.exports = SocketServices;