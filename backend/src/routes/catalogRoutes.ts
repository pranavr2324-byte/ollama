import { Router } from 'express';
import {
  getAlbumById,
  getArtistById,
  getSongById,
  listAlbums,
  listArtists,
  listSongs,
  searchCatalog,
  streamSong,
} from '../controllers/catalogController';
import { optionalAuth } from '../middleware/authMiddleware';
import { asyncHandler } from '../utils/asyncHandler';

const router = Router();

router.get('/songs', optionalAuth, asyncHandler(listSongs));
router.get('/songs/:id', optionalAuth, asyncHandler(getSongById));
router.get('/songs/:id/stream', optionalAuth, asyncHandler(streamSong));

router.get('/albums', optionalAuth, asyncHandler(listAlbums));
router.get('/albums/:id', optionalAuth, asyncHandler(getAlbumById));

router.get('/artists', optionalAuth, asyncHandler(listArtists));
router.get('/artists/:id', optionalAuth, asyncHandler(getArtistById));

router.get('/search', optionalAuth, asyncHandler(searchCatalog));

export default router;
