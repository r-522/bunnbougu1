const API_BASE = 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('sessionToken');
}

async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers['X-Session-Token'] = token;
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || 'APIエラー');
  return data;
}

function formToObject(form) {
  const fd = new FormData(form);
  return Object.fromEntries(fd.entries());
}

function renderTable(targetId, rows, columns) {
  const target = document.getElementById(targetId);
  if (!target) return;
  if (!rows.length) {
    target.innerHTML = '<p>データがありません。</p>';
    return;
  }
  const header = columns.map((c) => `<th>${c}</th>`).join('');
  const body = rows
    .map((row) => `<tr>${columns.map((c) => `<td>${row[c] ?? ''}</td>`).join('')}</tr>`)
    .join('');
  target.innerHTML = `<table><thead><tr>${header}</tr></thead><tbody>${body}</tbody></table>`;
}

function requireLoginNotice() {
  if (!getToken()) {
    alert('先にダッシュボードでログインしてください。');
  }
}

async function initDashboard() {
  const form = document.getElementById('login-form');
  const status = document.getElementById('login-status');
  const token = getToken();
  if (token) {
    status.textContent = 'ログイン済みです。';
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = formToObject(form);
    try {
      const data = await api('/session/login', { method: 'POST', body: JSON.stringify(payload) });
      localStorage.setItem('sessionToken', data.token);
      localStorage.setItem('staffCode', data.staffCode);
      status.textContent = `ログイン成功: ${data.displayName} (${data.staffCode})`;
      form.reset();
    } catch (err) {
      status.innerHTML = `<span class="error">${err.message}</span>`;
    }
  });
}

async function initProducts() {
  requireLoginNotice();
  const form = document.getElementById('product-form');
  const load = async () => {
    const rows = await api('/products');
    renderTable('products-table', rows, ['id', 'code', 'name', 'category', 'unitPrice']);
  };
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = formToObject(form);
    if (!d.code || !d.name) return alert('商品コードと商品名は必須です。');
    d.unitPrice = Number(d.unitPrice);
    await api('/products', { method: 'POST', body: JSON.stringify(d) });
    form.reset();
    load();
  });
  load();
}

async function initInventory() {
  requireLoginNotice();
  const form = document.getElementById('inventory-form');
  const adjustForm = document.getElementById('adjust-form');
  const load = async () => {
    const rows = await api('/inventories');
    renderTable('inventory-table', rows, ['id', 'productId', 'quantity', 'location']);
  };
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = formToObject(form);
    d.productId = Number(d.productId);
    d.quantity = Number(d.quantity);
    await api('/inventories', { method: 'POST', body: JSON.stringify(d) });
    form.reset();
    load();
  });
  adjustForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = formToObject(adjustForm);
    await api(`/inventories/${Number(d.id)}/adjust`, { method: 'POST', body: JSON.stringify({ delta: Number(d.delta) }) });
    adjustForm.reset();
    load();
  });
  load();
}

async function initOrders() {
  requireLoginNotice();
  const form = document.getElementById('order-form');
  const load = async () => {
    const rows = await api('/orders');
    renderTable('orders-table', rows, ['id', 'productId', 'quantity', 'customerName', 'orderDate', 'status']);
  };
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = formToObject(form);
    d.productId = Number(d.productId);
    d.quantity = Number(d.quantity);
    await api('/orders', { method: 'POST', body: JSON.stringify(d) });
    form.reset();
    load();
  });
  load();
}

async function initShipments() {
  requireLoginNotice();
  const form = document.getElementById('shipment-form');
  const load = async () => {
    const rows = await api('/shipments');
    renderTable('shipments-table', rows, ['id', 'orderId', 'productId', 'shippedQuantity', 'shippedDate', 'carrier']);
  };
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = formToObject(form);
    d.orderId = Number(d.orderId);
    d.productId = Number(d.productId);
    d.shippedQuantity = Number(d.shippedQuantity);
    await api('/shipments', { method: 'POST', body: JSON.stringify(d) });
    form.reset();
    load();
  });
  load();
}

const page = document.body.dataset.page;
if (page === 'dashboard') initDashboard();
if (page === 'products') initProducts();
if (page === 'inventory') initInventory();
if (page === 'orders') initOrders();
if (page === 'shipments') initShipments();
