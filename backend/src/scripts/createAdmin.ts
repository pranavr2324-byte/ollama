import bcrypt from 'bcryptjs';
import { prisma } from '../config/prisma';

async function main() {
  const args = process.argv.slice(2);
  const email = args[0];
  const password = args[1];
  const name = args[2] ?? 'VIBE Administrator';

  if (!email || !password) {
    console.error('❌ Usage: npm run create-admin <email> <password> [name]');
    console.error('Example: npm run create-admin admin@vibe.music AdminPassword123! "Super Admin"');
    process.exit(1);
  }

  const normalizedEmail = email.toLowerCase().trim();
  const passwordHash = await bcrypt.hash(password, 10);

  const user = await prisma.user.upsert({
    where: { email: normalizedEmail },
    update: {
      role: 'ADMIN',
      passwordHash,
      name,
    },
    create: {
      email: normalizedEmail,
      passwordHash,
      name,
      role: 'ADMIN',
    },
    select: {
      id: true,
      email: true,
      name: true,
      role: true,
    },
  });

  console.log('✅ Administrator account ready:');
  console.log(`   ID:    ${user.id}`);
  console.log(`   Email: ${user.email}`);
  console.log(`   Name:  ${user.name}`);
  console.log(`   Role:  ${user.role}`);
  console.log('\nYou can now login with this account to receive an ADMIN JWT token.');
}

main()
  .catch((err) => {
    console.error('❌ Failed to create administrator account:', err.message);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
