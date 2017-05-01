var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var GroupSchema = Schema(
    {
        members: [{type: Schema.ObjectId, ref: 'User', required: true}],
        name: {type: String, required: true},
        messages: [{type: Schema.ObjectId, ref: 'Message'}]
    },
    {
        collection: "tbGroup"
    }
);

module.exports = mongoose.model('Group', GroupSchema);