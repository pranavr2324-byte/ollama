import app from './app';
import { env } from './config/env';

const PORT = env.PORT;

const server = app.listen(PORT, () => {
  console.log(`🎵 VIBE Backend running on port ${PORT} [${env.NODE_ENV}]`);
  console.log(`🚀 API Base URL: http://localhost:${PORT}/api/v1`);
});

process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});

export default server;
