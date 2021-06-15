const CompressionPlugin = require(`compression-webpack-plugin`);

const path = require(`path`);
module.exports = {
  plugins: [
    new CompressionPlugin({
      test: /\.(js|css|html|svg|txt|eot|otf|ttf|gif)$/,
      filename: '[name][ext].gzip'
    })
  ]
};
