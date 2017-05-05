var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var GroupSchema = Schema(
    {
        members: [{type: Schema.ObjectId, ref: 'User', required: true}],
        name: {type: String, required: true}
    },
    {
        collection: "tbGroup"
    }
);

module.exports = mongoose.model('Group', GroupSchema);