var webpack = require('webpack');
//var HtmlWebpackPlugin = require('html-webpack-plugin');
module.exports = {
	entry: './source/app.js',
	output: {
		path: __dirname + '/build',
		filename: 'bundle.js'
	},
	module: {
		loaders: [
		{
        test: /.js?$/,
        loader: 'babel-loader',
        exclude: /node_modules/
		}
    ]
  }//,
  //plugins: [new HtmlWebpackPlugin()]
};