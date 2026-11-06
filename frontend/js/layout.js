const NAV_BY_ROLE = {
  STUDENT: [
    { key: "dashboard", label: "Dashboard", href: "student-dashboard.html", icon: "⌂" },
    { key: "profile", label: "My Profile", href: "student-profile.html", icon: "◐" },
    { key: "drives", label: "Placement Drives", href: "drives.html", icon: "▤" },
    { key: "applications", label: "My Applications", href: "my-applications.html", icon: "☰" }
  ],
  COMPANY: [
    { key: "dashboard", label: "Dashboard", href: "company-dashboard.html", icon: "⌂" },
    { key: "drives", label: "My Drives", href: "company-drives.html", icon: "▤" },
    { key: "create-drive", label: "Post a Drive", href: "company-create-drive.html", icon: "+" },
    { key: "applicants", label: "Applicants", href: "company-applicants.html", icon: "☰" },
    { key: "profile", label: "Company Profile", href: "company-profile.html", icon: "◐" }
  ],
  ADMIN: [
    { key: "dashboard", label: "Dashboard", href: "admin-dashboard.html", icon: "⌂" },
    { key: "students", label: "Students", href: "admin-students.html", icon: "◐" },
    { key: "companies", label: "Companies", href: "admin-companies.html", icon: "▦" },
    { key: "drives", label: "Placement Drives", href: "admin-drives.html", icon: "▤" },
    { key: "applications", label: "Applications", href: "admin-applications.html", icon: "☰" }
  ]
};

const ROLE_LABEL = { STUDENT: "Student", COMPANY: "Recruiter", ADMIN: "Placement Officer" };

function renderSidebar(activeKey) {
  const user = getUser();
  if (!user) return;

  const items = NAV_BY_ROLE[user.role] || [];
  const initials = (user.name || user.email || "U").trim().charAt(0).toUpperCase();

  const navHtml = items.map(item => `
    <a class="nav-link ${item.key === activeKey ? "active" : ""}" href="${item.href}">
      <span>${item.icon}</span><span>${item.label}</span>
    </a>
  `).join("");

  const html = `
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="mark">${initials}</div>
        <div>
          <div class="name">${user.name || user.email}</div>
          <div class="role">${ROLE_LABEL[user.role] || user.role}</div>
        </div>
      </div>
      <nav class="nav-group">${navHtml}</nav>
      <div class="sidebar-footer">
        <button class="nav-link" onclick="logout()"><span>⏻</span><span>Log out</span></button>
      </div>
    </aside>
  `;

  document.getElementById("sidebar-mount").outerHTML = html;
}
