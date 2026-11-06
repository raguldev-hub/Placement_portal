const API_BASE = "http://localhost:8081/api";

function getToken() {
  return localStorage.getItem("pp_token");
}

function getUser() {
  const raw = localStorage.getItem("pp_user");
  return raw ? JSON.parse(raw) : null;
}

function setSession(authResponse) {
  localStorage.setItem("pp_token", authResponse.token);
  localStorage.setItem("pp_user", JSON.stringify({
    userId: authResponse.userId,
    email: authResponse.email,
    role: authResponse.role,
    name: authResponse.name
  }));
}

function clearSession() {
  localStorage.removeItem("pp_token");
  localStorage.removeItem("pp_user");
}

function logout() {
  clearSession();
  window.location.href = "login.html";
}

/**
 * Core request helper.
 * @param {string} path - e.g. "/students/me"
 * @param {object} options - { method, body, isForm }
 */
async function apiRequest(path, options = {}) {
  const headers = {};
  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;

  let body = options.body;
  if (body && !options.isForm) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(body);
  }

  const res = await fetch(API_BASE + path, {
    method: options.method || "GET",
    headers,
    body: options.method && options.method !== "GET" ? body : undefined
  });

  if (res.status === 401) {
    clearSession();
    window.location.href = "login.html";
    throw new Error("Session expired");
  }

  let data = null;
  const text = await res.text();
  if (text) {
    try { data = JSON.parse(text); } catch (e) { data = text; }
  }

  if (!res.ok) {
    const message = (data && data.message) ? data.message : ("Request failed with status " + res.status);
    const err = new Error(message);
    err.status = res.status;
    err.fieldErrors = data && data.fieldErrors;
    throw err;
  }

  return data;
}

const api = {
  get: (path) => apiRequest(path, { method: "GET" }),
  post: (path, body) => apiRequest(path, { method: "POST", body }),
  put: (path, body) => apiRequest(path, { method: "PUT", body }),
  patch: (path, body) => apiRequest(path, { method: "PATCH", body }),
  del: (path) => apiRequest(path, { method: "DELETE" }),
  upload: (path, formData) => apiRequest(path, { method: "POST", body: formData, isForm: true })
};

function showToast(message, type = "default") {
  const toast = document.getElementById("toast");
  if (!toast) { alert(message); return; }
  toast.textContent = message;
  toast.className = "show" + (type !== "default" ? " " + type : "");
  clearTimeout(window._toastTimer);
  window._toastTimer = setTimeout(() => { toast.className = ""; }, 3500);
}

/** Guards a page to a required role; redirects otherwise. Call at top of protected pages. */
function requireRole(role) {
  const user = getUser();
  if (!user || !getToken()) {
    window.location.href = "login.html";
    return null;
  }
  if (role && user.role !== role) {
    window.location.href = "login.html";
    return null;
  }
  return user;
}
