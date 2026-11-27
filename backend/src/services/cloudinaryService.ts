import fs from 'fs';
import { UploadApiResponse } from 'cloudinary';
import { cloudinary } from '../config/cloudinary';
import { env, isCloudinaryConfigured } from '../config/env';
import { AppError } from '../utils/AppError';

export interface CloudinaryUploadResult {
  publicId: string;
  resourceType: string;
  format: string;
  durationSeconds: number;
  bytes: number;
  bitrate: number | null;
  secureUrl: string;
}

export interface CloudinaryAssetMetadata {
  publicId: string;
  resourceType: string;
  format: string;
  bytes: number;
  durationSeconds: number;
  secureUrl: string;
}

export interface ICloudinaryService {
  uploadAudioFromPath(
    filePath: string,
    options?: { filename?: string; folder?: string }
  ): Promise<CloudinaryUploadResult>;
  getAudioDeliveryUrl(publicId: string, resourceType?: string): string;
  getAssetMetadata(publicId: string, resourceType?: string): Promise<CloudinaryAssetMetadata>;
  deleteAudioAsset(publicId: string, resourceType?: string): Promise<void>;
}

const SAFE_PUBLIC_ID_REGEX = /^[a-zA-Z0-9_.\-/]+$/;

export class CloudinaryService implements ICloudinaryService {
  async uploadAudioFromPath(
    filePath: string,
    options?: { filename?: string; folder?: string }
  ): Promise<CloudinaryUploadResult> {
    if (!isCloudinaryConfigured()) {
      throw new AppError(
        'Cloudinary credentials are not configured on the server',
        503,
        'CLOUDINARY_NOT_CONFIGURED'
      );
    }

    const folder = options?.folder ?? env.CLOUDINARY_MUSIC_FOLDER;

    return new Promise((resolve, reject) => {
      const uploadStream = cloudinary.uploader.upload_stream(
        {
          folder,
          resource_type: 'video', // Cloudinary stores & streams audio via 'video' resource type
          use_filename: true,
          unique_filename: true,
          overwrite: false,
          ...(options?.filename ? { filename_override: options.filename } : {}),
        },
        (error, result?: UploadApiResponse) => {
          if (error || !result) {
            reject(
              new AppError(
                error?.message ?? 'Cloudinary audio upload failed',
                502,
                'CLOUDINARY_UPLOAD_FAILED'
              )
            );
            return;
          }

          const durationSeconds = Math.round(Number((result as Record<string, unknown>).duration ?? 0));
          const bitrateRaw = (result as Record<string, unknown>).bit_rate;
          const bitrate = typeof bitrateRaw === 'number' ? bitrateRaw : null;

          resolve({
            publicId: result.public_id,
            resourceType: result.resource_type || 'video',
            format: result.format || 'mp3',
            durationSeconds: Number.isFinite(durationSeconds) ? durationSeconds : 0,
            bytes: result.bytes ?? 0,
            bitrate,
            secureUrl: result.secure_url,
          });
        }
      );

      const readStream = fs.createReadStream(filePath);
      readStream.on('error', (streamErr) => {
        reject(new AppError(`Failed to read uploaded file: ${streamErr.message}`, 500, 'FILE_READ_ERROR'));
      });
      readStream.pipe(uploadStream);
    });
  }

  getAudioDeliveryUrl(publicId: string, resourceType = 'video'): string {
    if (!publicId || !SAFE_PUBLIC_ID_REGEX.test(publicId) || publicId.includes('..')) {
      throw new AppError('Invalid Cloudinary public ID', 400, 'INVALID_PUBLIC_ID');
    }

    if (!isCloudinaryConfigured()) {
      throw new AppError(
        'Cloudinary is not configured for audio delivery',
        503,
        'CLOUDINARY_NOT_CONFIGURED'
      );
    }

    return cloudinary.url(publicId, {
      resource_type: resourceType,
      secure: true,
      sign_url: env.CLOUDINARY_SIGNED_URLS,
    });
  }

  async getAssetMetadata(publicId: string, resourceType = 'video'): Promise<CloudinaryAssetMetadata> {
    if (!isCloudinaryConfigured()) {
      throw new AppError(
        'Cloudinary credentials are not configured on the server',
        503,
        'CLOUDINARY_NOT_CONFIGURED'
      );
    }

    try {
      const resource = await cloudinary.api.resource(publicId, {
        resource_type: resourceType,
      });

      return {
        publicId: resource.public_id,
        resourceType: resource.resource_type,
        format: resource.format,
        bytes: resource.bytes ?? 0,
        durationSeconds: Math.round(Number(resource.duration ?? 0)),
        secureUrl: resource.secure_url,
      };
    } catch (err: unknown) {
      const httpCode = (err as { error?: { http_code?: number } })?.error?.http_code;
      if (httpCode === 404) {
        throw new AppError('Audio asset not found in Cloudinary', 404, 'ASSET_NOT_FOUND');
      }
      throw new AppError('Failed to fetch Cloudinary asset metadata', 502, 'CLOUDINARY_METADATA_ERROR');
    }
  }

  async deleteAudioAsset(publicId: string, resourceType = 'video'): Promise<void> {
    if (!isCloudinaryConfigured()) return;
    try {
      await cloudinary.uploader.destroy(publicId, {
        resource_type: resourceType,
        invalidate: true,
      });
    } catch (err) {
      console.error(`Failed to delete Cloudinary asset ${publicId}:`, err);
    }
  }
}

export const cloudinaryService: ICloudinaryService = new CloudinaryService();
