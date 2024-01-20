/** @type {import('tailwindcss').Config} */
module.exports = {
  // content: ["./src/**/*.{html,js}"],
  content: ["./src/**/*.html", "./srcc/**/*.js"],
  darkMode: 'class',
  theme: {
  },
  plugins: [
    require('tailwindcss'),
    require('autoprefixer'),
  ],
}

