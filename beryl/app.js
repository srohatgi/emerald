
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')

var app = module.exports = express.createServer();

// Configuration

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
});

// Routes
app.get('/', routes.index);

// single sign on - using YouSendIt credentials
app.post('/login', routes.login);

// get all posts, filter = '_current' for only mine
app.get('/post/:filter', routes.getPost);
// update metadata for documents
app.put('/post/:docid', routes.updatePost);
// add new documents, links, etc.
app.post('/post', routes.addPost);

// enrich user profile 
// - like gravitar avatars
// - like follow users
app.put('/users/_current', routes.changeUser);
// get any pending tasks from others in your network
app.get('/users/_current', routes.getUser);

// start a group
app.post('/group', routes.addGroup);
// get groupinfo
app.get('/group', routes.getGroup);

app.listen(3000);
console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);