// Script para limpar cache e localStorage
console.log('🧹 Limpando cache e localStorage...');

// Limpar localStorage
localStorage.removeItem('auth_token');
localStorage.removeItem('auth_expires');
localStorage.removeItem('auth_user');
localStorage.removeItem('questionResponses');
localStorage.removeItem('selectedPlan');

console.log('✅ localStorage limpo!');
console.log('🔄 Recarregue a página para aplicar as mudanças.'); 