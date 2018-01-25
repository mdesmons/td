var path = require('path');
var webpack = require('webpack');

var node_dir = __dirname + '/node_modules';



module.exports = {
    entry: './src/app.js',
 //   devtool: 'sourcemaps',
    cache: true,
    module: {
        loaders: [
            {
      			test: /\.(js|jsx)$/,
              	exclude: /(node_modules)/,
                loader: 'babel-loader',
                query: {
                    cacheDirectory: true,
                    presets: ['es2015', 'react']
                }
            }
        ],
        rules: [
            { test: /\.js$/, exclude: /node_modules/, loader: "babel-loader" },
            { test: /\.css$/, use: [ 'style-loader', 'css-loader']},
            { test: /\.(png|svg|jpg|gif)$/, use: [ 'file-loader']}
          ]
    },
    plugins: [
          new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            Popper: ['popper.js', 'default'],
            // In case you imported plugins individually, you must also require them here:
            Util: "exports-loader?Util!bootstrap/js/dist/util",
            Dropdown: "exports-loader?Dropdown!bootstrap/js/dist/dropdown",
          }),
  //        new webpack.optimize.UglifyJsPlugin()
    ]
};
