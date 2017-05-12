var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var RequestSchema = Schema(
    {
        sender : {type: Schema.ObjectId, ref: 'User', required: true},
        receiver: {type: Schema.ObjectId, ref: 'User', required: true},
        //-- there are 2 request type, "friend" and "group"
        //-- request data for "friend"
        //--    requestData[0]: sender name
        //--    requestData[1]: receiver name

        //-- request data for "group"
        //--    requestData[0]: groupID
        //--    requestData[1]: groupName
        //--    requestData[2]: sender name
        //--    requestData[3]: receiver name

        type: {type: String, required: true},
        status: {type: String, default: "pending"},
        responseDate: {type: Date},
        requestData: [{type: String}]
    },
    {
        collection: "Request"
    }
);

module.exports = mongoose.model('Request', RequestSchema);