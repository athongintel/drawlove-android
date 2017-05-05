var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var RequestSchema = Schema(
    {
        sender : {type: Schema.ObjectId, ref: 'User', required: true},
        receiver: {type: Schema.ObjectId, ref: 'User', required: true},
        type: {type: String, required: true},
        status: {type: String, default: "pending"},
        requestDate: {type: Date, default: Date.now},
        requestData: [{type: String}]
    },
    {
        collection: "Request"
    }
);

module.exports = mongoose.model('Request', RequestSchema);