var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var RequestSchema = Schema(
    {
        sender : {type: Schema.ObjectId, ref: 'User', required: true},
        accepter: {type: Schema.ObjectId, ref: 'User', required: true},
        type: {type: String, required: true},
        requestData_1: {type: String},
        requestData_2: {type: String},
        status: {type: String, required: true}
    },
    {
        collection: "Request"
    }
);

module.exports = mongoose.model('Request', RequestSchema);