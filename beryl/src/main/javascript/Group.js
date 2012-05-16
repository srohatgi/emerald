var http = require('http');

var Group = function(host,port) {
  var options = {
    host:host,
    port:port,
    headers: { 
      'Content-Type': 'application/x-www-form-urlencoded',
      'Accept': '*/*'
    }
  };
  
  return {
    browse: function(prev,next,count, callback) {
      options.path = '/net/groups?count='+count;
      options.method = 'GET';
      var req = http.request(options,function(res) {
        var body = '';
        res.on('data',function (chunk) { body+=chunk; });
        res.on('end',function() {
          var json = JSON.parse(body);
          if ( res.statusCode/100 != 2 ) callback(json);
          else {
            callback(null,json);
          }
        });
      });
      req.end();
    },
    setAuthToken: function(token) {
      options.headers['X-Auth-Token'] = token;
    },
    addGroup: function(name,description,callback) {
      options.path = '/net/groups/new';
      options.method = 'POST';
      var req = http.request(options,function(res) {
        var body = '';
        res.on('data',function (chunk) { body+=chunk; });
        res.on('end',function() {
          console.log("add group  body="+body);
          var json = JSON.parse(body);
          if ( res.statusCode/100 != 2 ) callback(json);
          else {
            callback(null,json);
          }
        });
      });
      req.write("name="+name+"&description="+description);
      req.end();
    }
  }
};

exports.Group = Group;