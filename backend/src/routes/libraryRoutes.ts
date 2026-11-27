import { Router } from 'express';
import {
  addFavourite,
  addSongToPlaylist,
  createPlaylist,
  deletePlaylist,
  getPlaylistById,
  listFavourites,
  listPlaylists,
  listRecentlyPlayed,
  recordRecentlyPlayed,
  removeFavourite,
  removeSongFromPlaylist,
  updatePlaylist,
} from '../controllers/libraryController';
import { optionalAuth, requireAuth } from '../middleware/authMiddleware';
import { asyncHandler } from '../utils/asyncHandler';

const router = Router();

// ==========================================
// PLAYLISTS
// ==========================================
router.get('/playlists', requireAuth, asyncHandler(listPlaylists));
router.post('/playlists', requireAuth, asyncHandler(createPlaylist));
router.get('/playlists/:id', optionalAuth, asyncHandler(getPlaylistById));
router.patch('/playlists/:id', requireAuth, asyncHandler(updatePlaylist));
router.delete('/playlists/:id', requireAuth, asyncHandler(deletePlaylist));
router.post('/playlists/:id/songs', requireAuth, asyncHandler(addSongToPlaylist));
router.delete('/playlists/:id/songs/:songId', requireAuth, asyncHandler(removeSongFromPlaylist));

// ==========================================
// FAVOURITES
// ==========================================
router.get('/favourites', requireAuth, asyncHandler(listFavourites));
router.post('/favourites/:songId', requireAuth, asyncHandler(addFavourite));
router.delete('/favourites/:songId', requireAuth, asyncHandler(removeFavourite));

// ==========================================
// RECENTLY PLAYED
// ==========================================
router.get('/recently-played', requireAuth, asyncHandler(listRecentlyPlayed));
router.post('/recently-played/:songId', requireAuth, asyncHandler(recordRecentlyPlayed));

export default router;
