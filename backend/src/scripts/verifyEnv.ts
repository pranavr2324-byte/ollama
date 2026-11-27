import dotenv from 'dotenv';
dotenv.config();

function maskSecret(val: string | undefined): string {
  if (!val) return '❌ NOT SET';
  if (val.length < 8) return '✅ SET (length: ' + val.length + ')';
  return `✅ SET (${val.slice(0, 3)}...${val.slice(-3)}, length: ${val.length})`;
}

function maskDbUrl(url: string | undefined): string {
  if (!url) return '❌ NOT SET';
  try {
    const parsed = new URL(url);
    const host = parsed.hostname;
    const db = parsed.pathname;
    const port = parsed.port || '5432';
    return `✅ SET (host: ${host}, port: ${port}, db: ${db})`;
  } catch {
    return '⚠️ SET but invalid URL format';
  }
}

export function runEnvCheck() {
  console.log('========================================');
  console.log('   VIBE Cloud Environment Status Check   ');
  console.log('========================================');

  const dbUrl = process.env.DATABASE_URL;
  const isDbSet = Boolean(dbUrl && !dbUrl.includes('your-project') && !dbUrl.includes('localhost'));
  console.log(`DATABASE_URL:               ${maskDbUrl(dbUrl)}`);

  const supaUrl = process.env.SUPABASE_URL;
  const isSupaUrlSet = Boolean(supaUrl && !supaUrl.includes('your-project'));
  console.log(`SUPABASE_URL:               ${isSupaUrlSet ? `✅ SET (${supaUrl})` : (supaUrl ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const supaAnon = process.env.SUPABASE_ANON_KEY;
  const isSupaAnonSet = Boolean(supaAnon && !supaAnon.includes('your-supabase'));
  console.log(`SUPABASE_ANON_KEY:          ${isSupaAnonSet ? maskSecret(supaAnon) : (supaAnon ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const supaService = process.env.SUPABASE_SERVICE_ROLE_KEY;
  const isSupaServiceSet = Boolean(supaService && !supaService.includes('your-supabase'));
  console.log(`SUPABASE_SERVICE_ROLE_KEY:  ${isSupaServiceSet ? maskSecret(supaService) : (supaService ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const cCloud = process.env.CLOUDINARY_CLOUD_NAME;
  const isCCloudSet = Boolean(cCloud && !cCloud.includes('your-cloud-name'));
  console.log(`CLOUDINARY_CLOUD_NAME:      ${isCCloudSet ? `✅ SET (${cCloud})` : (cCloud ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const cKey = process.env.CLOUDINARY_API_KEY;
  const isCKeySet = Boolean(cKey && !cKey.includes('your-api-key'));
  console.log(`CLOUDINARY_API_KEY:         ${isCKeySet ? maskSecret(cKey) : (cKey ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const cSecret = process.env.CLOUDINARY_API_SECRET;
  const isCSecretSet = Boolean(cSecret && !cSecret.includes('your-api-secret'));
  console.log(`CLOUDINARY_API_SECRET:      ${isCSecretSet ? maskSecret(cSecret) : (cSecret ? '⚠️ PLACEHOLDER DETECTED' : '❌ NOT SET')}`);

  const jwt = process.env.JWT_SECRET;
  console.log(`JWT_SECRET:                 ${jwt && jwt.length >= 32 ? maskSecret(jwt) : '❌ INVALID OR TOO SHORT'}`);

  console.log('========================================');
  const allCloudinary = isCCloudSet && isCKeySet && isCSecretSet;
  const allSupabase = isDbSet && isSupaUrlSet && isSupaAnonSet;

  if (allCloudinary && allSupabase) {
    console.log('🎉 ALL CLOUD CREDENTIALS ARE READY FOR TESTING!');
  } else {
    console.log('⏳ Awaiting missing credentials in backend/.env');
  }
}

if (require.main === module || process.argv[1]?.endsWith('verifyEnv.ts')) {
  runEnvCheck();
}
