/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#4f46e5', // Indigo 600
        secondary: '#0ea5e9', // Sky 500
        accent: '#f59e0b', // Amber 500
        background: '#f8fafc', // Slate 50
        surface: '#ffffff',
      }
    },
  },
  plugins: [],
}
