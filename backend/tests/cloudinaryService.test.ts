import { describe, expect, it } from 'vitest';
import { CloudinaryService } from '../src/services/cloudinaryService';

describe('CloudinaryService', () => {
  const service = new CloudinaryService();

  it('generates a valid Cloudinary streaming delivery URL for audio/video asset', () => {
    const publicId = 'vibe/music/tamil_melody_track_1';
    const url = service.getAudioDeliveryUrl(publicId, 'video');

    expect(url).toContain('https://res.cloudinary.com/');
    expect(url).toContain('/video/upload/');
    expect(url).toContain(publicId);
  });

  it('rejects invalid or dangerous public IDs to prevent SSRF and injection', () => {
    expect(() => service.getAudioDeliveryUrl('')).toThrow();
    expect(() => service.getAudioDeliveryUrl('../../etc/passwd')).toThrow();
    expect(() => service.getAudioDeliveryUrl('invalid public id with spaces')).toThrow();
  });
});
