let useCaseIdCounter = 0;

function getSectionAValues() {
  const values = {};
  document.querySelectorAll('.input-field').forEach(input => {
    const key = input.parentElement.getAttribute('data-label');
    values[key] = input.value;
  });
  return values;
}

function generateUrl(button, env) {
  const row = button.parentElement;
  const useCase = row.querySelector('.use-case-select').value;
  const tag = row.querySelector('.tag-input').value;

  const sectionA = getSectionAValues();
  const ansiblePkg = sectionA['ansible package name'] || '';
  const virtualEnv = sectionA['virtual env'] || '';

  const url = `https://yourtool.local/execute?env=${env}&tag=${tag}&use_case=${useCase}&ansible_package=${ansiblePkg}&virtual_env=${virtualEnv}`;

  const span = row.querySelector(`.url-${env.toLowerCase()}`);
  span.textContent = url;
  button.disabled = true;
}

function addUseCaseRow() {
  const container = document.getElementById('use-case-container');

  const rowId = `usecase-${useCaseIdCounter++}`;
  const row = document.createElement('div');
  row.className = 'd-flex align-items-center flex-wrap gap-2 mb-2';
  row.id = rowId;

  row.innerHTML = `
    <select class="form-select use-case-select w-auto">
      <option value="deploy">deploy</option>
      <option value="switch">switch</option>
      <option value="delete">delete</option>
      <option value="spn">spn</option>
      <option value="reset">reset</option>
    </select>
    <input class="form-control tag-input w-auto" placeholder="Tag" />

    <button class="btn btn-outline-primary" onclick="generateUrl(this, 'P0')">P0</button>
    <span class="url-p0 small text-break"></span>

    <button class="btn btn-outline-secondary" onclick="generateUrl(this, 'R0')">R0</button>
    <span class="url-r0 small text-break"></span>

    <button class="btn btn-outline-warning" onclick="generateUrl(this, 'T1')">T1</button>
    <span class="url-t1 small text-break"></span>

    <button class="btn btn-sm btn-danger" onclick="this.parentElement.remove()">🗑️</button>
  `;

  container.appendChild(row);
}

// Add initial row on page load
window.onload = () => {
  addUseCaseRow();
};
