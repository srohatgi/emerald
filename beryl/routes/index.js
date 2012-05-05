var Ysi = require('./YsiApi').Api;
var Session = require('./NetworkApi').Api;
var events = require('events');
var emitter = new events.EventEmitter();

var Evt = function(req, res, obj) {
  var _error = null, _results = null;
  return {
    req: req,
    res: res,
    obj: obj,
    error: error,
    SetError: function(err) {
      _error = err;
    },
    SetResults: function(results) {
      _results = results;
    }
  };
}

emitter.on('buildSession', function(evt) {
  Session.UpsertObj(evt.results,'Session',function(err, reply) {
    evt.err = err;
    if ( !err ) console.log("Session Obj Id:"+reply);
    emitter.emit('results',evt);
  });
});

var Login = function(user, password, evt) {
  var data = 'email='+encodeURIComponent(req.param('user'))+'&password='+encodeURIComponent(req.param('password'));
  var evt = Evt(req,res);
  Ysi('POST', '/dpi/v1/auth', data, function(error, response) {
    evt.SetError(error);
    if ( !error ) evt.results = { obj_id: response.authToken, email: req.param('user'), loginTs: new Date().getTime() };
    emitter.emit('buildSession', evt);
  });
};

var Session = function(obj_id, email, loginTs, evt) {
  
};

exports.Documents = function(req, res) {
  var folder_id = 0;
  
};

emitter.on('getDocuments', function(obj) {
  if ( (!obj.req.params.filter || obj.req.params.filter === '_current') && 
        !obj.folder_id 
     ) 
    obj.folder_id = 0;

  Ysi('GET','/dpi/v1/folder/'+obj.folder_id,null,function(err, ret) {
    
    if ( !err ) {
      if ( !obj.req.params.filter || obj.req.params.filter === '_current' ) {
        if ( obj.folder_id !== 0 ) {
          obj.res.end(JSON.stringify(ret,null,2)+'\n');
          return;
        }
        //console.log(JSON.stringify(ret,null,2));
        for (var i=0;i<ret.folders.folder.length;i++) {
          if ( ret.folders.folder[i].name !== '__bin__' ) continue;
          // setup recursion
          obj.folder_id = ret.folders.folder[i].id;
          emitter.emit('getDocuments',obj);
          return;
        }
      }      
    }
    
    // catch all errors
    if ( !err ) err = 'user setup error: no __bin__ folder';  
    console.log('ERROR:'+err);
    obj.res.end(err);
  }, obj.authToken);
});

emitter.on('results', function(evt) {
  var result;
  if ( evt.err ) result = {error: 'error handling '+obj.err};
  else result = obj.result;
  evt.res.end(JSON.stringify(result,null,2)+'\n');
});

