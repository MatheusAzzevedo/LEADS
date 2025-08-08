#!/usr/bin/env node
/*
  Copia o build do Vite (dist/) para o backend em
  backend/src/main/resources/static, garantindo limpeza prévia.
*/

const fs = require('fs');
const fsp = require('fs/promises');
const path = require('path');

async function ensureDir(dirPath) {
  await fsp.mkdir(dirPath, { recursive: true });
}

async function removeIfExists(targetPath) {
  try {
    await fsp.rm(targetPath, { recursive: true, force: true });
  } catch (_) {
    // ignore
  }
}

async function copyDir(src, dest) {
  // Node 16+: cp com recursive
  if (fs.cp) {
    await fsp.cp(src, dest, { recursive: true });
    return;
  }
  // Fallback simples
  const entries = await fsp.readdir(src, { withFileTypes: true });
  await ensureDir(dest);
  for (const entry of entries) {
    const srcPath = path.join(src, entry.name);
    const destPath = path.join(dest, entry.name);
    if (entry.isDirectory()) {
      await copyDir(srcPath, destPath);
    } else if (entry.isFile()) {
      await fsp.copyFile(srcPath, destPath);
    }
  }
}

async function main() {
  const projectRoot = process.cwd();
  const distDir = path.join(projectRoot, 'dist');
  const backendStaticDir = path.join(projectRoot, 'backend', 'src', 'main', 'resources', 'static');

  const existsDist = fs.existsSync(distDir);
  if (!existsDist) {
    console.error('dist/ não encontrado. Execute: npm run build');
    process.exit(1);
  }

  await ensureDir(path.join(projectRoot, 'backend', 'src', 'main', 'resources'));
  await removeIfExists(backendStaticDir);
  await ensureDir(backendStaticDir);
  await copyDir(distDir, backendStaticDir);

  console.log('✅ Build do frontend sincronizado para backend/src/main/resources/static');
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});


