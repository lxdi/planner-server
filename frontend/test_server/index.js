// On windows use 'start node index.js' to start server in new window

const http = require('http')
const fs = require('fs')
const port = 3000

const createTargetLazy = function(_id, _title, _parentid){
  return {
    id: _id,
    title: _title,
    parentid: _parentid
  }
}

const createMeanLazy = function(_id, _title, _parentid, _targetsid){
  var targetsToAdd = _targetsid!=null?_targetsid:[]
  return {
    id: _id,
    title: _title,
    parentid: _parentid,
    targetsid:targetsToAdd
  }
}

const testdata = {
  '/target/all/lazy': JSON.stringify([createTargetLazy(1, "test target 1"), createTargetLazy(2, "test target 2", 1)]),
  '/mean/all/lazy': JSON.stringify([
    createMeanLazy(1, "test mean 1", null, [1,2]),
    createMeanLazy(2, "test mean 2", 1, [2]),
    createMeanLazy(3, "test mean 3", null, [1])
  ])
}

const requestHandler = (request, response) => {
    console.log(request.url)
    //response.writeHead(200, {'Content-Type': 'text/html'});
    if(testdata[request.url]!=null){
      response.end(testdata[request.url]);
    } else{
      const pathname = request.url=='/'? '../build/index.html': '../build'+request.url
      fs.readFile(pathname, function(err, data){
            if(err){
              response.statusCode = 500;
              response.end(`Error getting the file: ${err}.`);
            } else {
              //console.log(data)
              response.end(data);
            }
      });
    }
}
const server = http.createServer(requestHandler)
server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    console.log(`server is listening on ${port}`)
})
