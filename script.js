let useCaseIdCounter = 0;

// Function to retrieve values from Section A
function getSectionAValues() {
  const values = {};
  document.querySelectorAll('.input-field').forEach(input => {
    const key = input.getAttribute('data-label');
    values[key] = input.value;
  });
  return values;
}

// Function to generate URL and disable the button
function generateUrl(button, env) {
  const row = button.closest('.use-case-row');
  const useCase = row.querySelector('.use-case-select').value;
  const tag = row.querySelector('.tag-input')?.value || '';

  const sectionA = getSectionAValues();
  const ansiblePkg = sectionA['ansible package name'] || '';
  const virtualEnv = sectionA['virtual env'] || '';

  const url = `https://yourtool.local/execute?env=${env}&tag=${tag}&use_case=${useCase}&ansible_package=${ansiblePkg}&virtual_env=${virtualEnv}`;

  const span = row.querySelector(`.url-${env.toLowerCase()}`);
  span.textContent = url;
  button.disabled = true;
}

// Function to handle dropdown change
function handleUseCaseChange(selectElement) {
  const row = selectElement.closest('.use-case-row');
  const selectedValue = selectElement.value;

  const tagInput = row.querySelector('.tag-input');
  const p0Button = row.querySelector('.btn-p0');
  const r0Button = row.querySelector('.btn-r0');
  const t1Button = row.querySelector('.btn-t1');
  const taskDesc = row.querySelector('.task-desc');

  if (selectedValue === 'verification' || selectedValue === 'signoff') {
    tagInput.style.display = 'none';
    p0Button.style.display = 'none';
    r0Button.style.display = 'none';
    t1Button.style.display = 'none';
    taskDesc.style.display = 'block';
  } else {
    tagInput.style.display = 'inline-block';
    p0Button.style.display = 'inline-block';
    r0Button.style.display = 'inline-block';
    t1Button.style.display = 'inline-block';
    taskDesc.style.display = 'none';
  }
}

// Function to add a new use case row
function addUseCaseRow() {
  const container = document.getElementById('use-case-container');

  const rowId = `usecase-${useCaseIdCounter++}`;
  const row = document.createElement('div');
  row.className = 'd-flex align-items-center flex-wrap gap-2 mb-2 use-case-row';
  row.id = rowId;

  row.innerHTML = `
    <select class="form-select use-case-select w-auto">
      <option value="deploy">deploy</option>
      <option value="switch">switch</option>
      <option value="delete">delete</option>
      <option value="spn">spn</option>
      <option value="reset">reset</option>
      <option value="verification">verification</option>
      <option value="signoff">signoff</option>
    </select>
    <input class="form-control tag-input w-auto" placeholder="Tag" />
    <textarea class="form-control task-desc w-100" placeholder="Task Description" style="display:none;"></textarea>
    <input type="datetime-local" class="form-control when-input w-auto" />
    <input type="text" class="form-control actor-input w-auto" placeholder="Actor" />

    <button class="btn btn-outline-primary btn-p0" onclick="generateUrl(this, 'P0')">P0</button>
    <span class="url-p0 url-display"></span>

    <button class="btn btn-outline-secondary btn-r0" onclick="generateUrl(this, 'R0')">R0</button>
    <span class="url-r0 url-display"></span>

    <button class="btn btn-outline-warning btn-t1" onclick="generateUrl(this, 'T1')">T1</button>
    <span class="url-t1 url-display"></span>

    <button class="btn btn-sm btn-danger" onclick="this.parentElement.remove()">🗑️</button>
  `;

  container.appendChild(row);

  // Add event listener for dropdown change
  const selectElement = row.querySelector('.use-case-select');
  selectElement.addEventListener('change', function() {
    handleUseCaseChange(this);
  });

  // Initialize the display based on default selection
  handleUseCaseChange(selectElement);
}

// Function to create the ROTA table
function createROTA() {
    const tableBody = document.querySelector('#rota-table tbody');
    tableBody.innerHTML = ''; // Clear existing rows
    let serialNo = 1;
  
    document.querySelectorAll('.use-case-row').forEach(row => {
      const useCase = row.querySelector('.use-case-select').value;
      const when = row.querySelector('.when-input').value;
      const actor = row.querySelector('.actor-input').value;
      const tagInput = row.querySelector('.tag-input');
      const taskDescInput = row.querySelector('.task-desc');
      const urlP0 = row.querySelector('.url-p0').textContent;
      const urlR0 = row.querySelector('.url-r0').textContent;
  
      const isSpecialCase = (useCase === 'verification' || useCase === 'signoff');
  
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${serialNo++}</td>
        <td>${useCase}</td>
        <td>${isSpecialCase ? taskDescInput.value : ''}</td>
        <td>${actor}</td>
        <td>${when}</td>
        <td>${isSpecialCase ? '' : urlP0}</td>
        <td>${isSpecialCase ? '' : urlR0}</td>
        <td></td>
        <td></td>
      `;
      tableBody.appendChild(tr);
    });
  }
  

