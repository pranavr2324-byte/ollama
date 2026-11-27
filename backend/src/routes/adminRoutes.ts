import { Router } from 'express';
import {
  deleteSong,
  publishSong,
  updateSong,
  uploadSong,
} from '../controllers/adminController';
import { requireAdmin, requireAuth } from '../middleware/authMiddleware';
import { audioUploadMiddleware } from '../middleware/uploadMiddleware';
import { asyncHandler } from '../utils/asyncHandler';

const router = Router();

router.use(requireAuth, requireAdmin);

router.post(
  '/songs/upload',
  audioUploadMiddleware.single('audio'),
  asyncHandler(uploadSong)
);
router.patch('/songs/:id', asyncHandler(updateSong));
router.delete('/songs/:id', asyncHandler(deleteSong));
router.post('/songs/:id/publish', asyncHandler(publishSong));

export default router;
