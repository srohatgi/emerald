var Ysi = require('./YsiApi').Api;
var events = require('events');
var docListEmitter = new events.EventEmitter();

exports.index = function(req, res){
  res.render('index', { title: 'Express' })
};

exports.login = function(req, res) {
  var data = 'email='+encodeURIComponent(req.param('user'))+'&password='+encodeURIComponent(req.param('password'));
  Ysi('POST', '/dpi/v1/auth', data, function(error, response) {
    if ( !error ) res.end(JSON.stringify(response,null,2)+'\n');
    else res.end(error);
  });
};

docListEmitter.on('getDocuments', function(obj) {
  Ysi('GET','/dpi/v1/folder/'+obj.id,null,function(err, ret) {
    if ( err ) {
      console.log('ERROR:'+err);
      obj.res.end(err);
      return;
    }
    if ( obj.id !== 0 ) {
      obj.res.end(JSON.stringify(ret,null,2)+'\n');
      return;
    }
    for (var i=0;i<ret.folders.folder.length;i++) {
      if ( ret.folders.folder[i].name !== '__bin__' ) continue;
      docListEmitter.emit('getDocuments',{ 
        req: obj.req, 
        res: obj.res, 
        id: ret.folders.folder[i].id 
      });
      break;
    }
  }, obj.req.headers['x-auth-token']);
});

exports.documents = function(req, res) {
  docListEmitter.emit('getDocuments',{ req: req, res: res, id: 0} );
};
