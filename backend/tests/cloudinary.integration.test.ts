import { describe, expect, it } from 'vitest';
import { isCloudinaryConfigured } from '../src/config/env';
import { CloudinaryService } from '../src/services/cloudinaryService';

describe('Cloudinary Integration Test', () => {
  const isRealTest = process.env.RUN_REAL_CLOUDINARY_TEST === 'true';

  it('verifies Cloudinary configuration and skip/execute integration accordingly', async () => {
    const service = new CloudinaryService();

    if (!isRealTest || !isCloudinaryConfigured()) {
      console.log('Skipping real Cloudinary upload integration test (no live credentials or not enabled).');
      expect(true).toBe(true);
      return;
    }

    // When real credentials are provided via RUN_REAL_CLOUDINARY_TEST=true
    const url = service.getAudioDeliveryUrl('vibe/music/test_sample', 'video');
    expect(url).toContain('res.cloudinary.com');
  });
});
