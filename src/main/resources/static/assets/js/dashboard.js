// Elementos generales
const grupoInput = document.getElementById('buscar-grupos');
const individualInput = document.getElementById('buscar-individuales');
const historialInput = document.getElementById('buscar-historial');
const listaGrupos = document.getElementById('lista-grupos');
const listaIndividuales = document.getElementById('lista-individuales');
const listaHistorial = document.getElementById('lista-historial');
const asignarSelect = document.getElementById('asignar_a');
let listItems = document.querySelectorAll('.list-group-item-action');

// MODAL: Añadir Individuo
const addIndividualModal = document.getElementById('addIndividualModal');
const addIndividualBtn = document.getElementById('add-individual-btn');
const closeButtons = document.querySelectorAll('.close-button');
const addIndividualForm = document.getElementById('addIndividualForm');
const groupSelectionList = document.getElementById('group-selection-list');
const buscarGruposIndividualInput = document.getElementById('buscar-grupos-individual');

// MODAL: Añadir Grupo
const addGroupModal = document.getElementById('addGroupModal');
const addGroupBtn = document.getElementById('add-group-btn');
const closeGroupButtons = document.querySelectorAll('.close-group-button');
const addGroupForm = document.getElementById('addGroupForm');
const individualSelectionList = document.getElementById('individual-selection-list');
const buscarIndividuosGrupoInput = document.getElementById('buscar-individuos-grupo');

// =================== Funciones reutilizables ===================

function createListButton({ text, iconClass, classes = [], clickHandler }) {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.classList.add('list-group-item', 'list-group-item-action', ...classes);
    btn.innerHTML = `<i class="${iconClass}"></i> ${text}`;
    btn.addEventListener('click', () => {
        updateActiveClass(btn);
        syncSelectWithItem(text);
    });
    clickHandler?.(btn);
    return btn;
}

function updateActiveClass(activeEl) {
    document.querySelectorAll('.list-group-item-action').forEach(el => el.classList.remove('active'));
    activeEl.classList.add('active');
}

function syncSelectWithItem(selectedValue) {
    for (let i = 0; i < asignarSelect.options.length; i++) {
        const option = asignarSelect.options[i];
        if (option.textContent === selectedValue || 
            (selectedValue.startsWith('Grupo') && option.value.startsWith('grupo')) || 
            (selectedValue.startsWith('Persona') && option.value.startsWith('individual'))) {
            asignarSelect.value = option.value;
            break;
        }
    }
}

function filterList(input, list, itemClass) {
    const filter = input.value.toUpperCase();
    const items = list.getElementsByClassName(itemClass);
    for (let item of items) {
        const text = item.textContent.toUpperCase();
        item.style.display = text.includes(filter) ? "" : "none";
    }
}

// =================== Modal: Añadir Individuo ===================

addIndividualBtn.addEventListener('click', () => {
    groupSelectionList.innerHTML = '';
    document.querySelectorAll('#lista-grupos .grupo-item').forEach(grupo => {
        const nombreGrupo = grupo.textContent.trim();
        const radioItem = document.createElement('div');
        radioItem.classList.add('group-radio-item');
        radioItem.innerHTML = `
            <input type="radio" name="grupo-seleccionado" value="${nombreGrupo}">
            <label>${nombreGrupo}</label>`;
        groupSelectionList.appendChild(radioItem);
    });
    addIndividualModal.style.display = "block";
});

closeButtons.forEach(btn => btn.addEventListener('click', () => addIndividualModal.style.display = "none"));
window.addEventListener('click', e => { if (e.target === addIndividualModal) addIndividualModal.style.display = "none" });

addIndividualForm.addEventListener('submit', e => {
    e.preventDefault();
    const nombre = document.getElementById('nombre-individual').value;
    const telefono = document.getElementById('telefono-individual').value;
    const grupo = document.querySelector('input[name="grupo-seleccionado"]:checked')?.value || '';

    console.log('Nuevo individuo:', nombre, 'Grupo:', grupo, 'Teléfono:', telefono);

    const btn = createListButton({
        text: nombre,
        iconClass: 'bi bi-person-circle',
        classes: ['individual-item']
    });

    listaIndividuales.appendChild(btn);
    addIndividualModal.style.display = "none";
    addIndividualForm.reset();
});

// =================== Modal: Añadir Grupo ===================

addGroupBtn.addEventListener('click', () => {
    individualSelectionList.innerHTML = '';
    document.querySelectorAll('#lista-individuales .individual-item').forEach(ind => {
        const nombre = ind.textContent.trim();
        const item = document.createElement('div');
        item.classList.add('individual-checkbox-item');
        item.innerHTML = `
            <input type="checkbox" value="${nombre}">
            <label>${nombre}</label>`;
        individualSelectionList.appendChild(item);
    });
    addGroupModal.style.display = "block";
});

closeGroupButtons.forEach(btn => btn.addEventListener('click', () => addGroupModal.style.display = "none"));
window.addEventListener('click', e => { if (e.target === addGroupModal) addGroupModal.style.display = "none" });

addGroupForm.addEventListener('submit', e => {
    e.preventDefault();
    const nombreGrupo = document.getElementById('nombre-grupo').value;
    const seleccionados = Array.from(individualSelectionList.querySelectorAll('input:checked'))
        .map(cb => cb.value);

    console.log('Nuevo grupo:', nombreGrupo, 'con individuos:', seleccionados);

    const btn = createListButton({
        text: nombreGrupo,
        iconClass: 'bi bi-tag-fill',
        classes: ['grupo-item']
    });

    listaGrupos.appendChild(btn);
    addGroupModal.style.display = "none";
    addGroupForm.reset();
});

// =================== Filtros de búsqueda ===================

grupoInput.addEventListener('keyup', () => filterList(grupoInput, listaGrupos, 'grupo-item'));
individualInput.addEventListener('keyup', () => filterList(individualInput, listaIndividuales, 'individual-item'));
historialInput.addEventListener('keyup', () => filterList(historialInput, listaHistorial, 'historial-item'));

buscarIndividuosGrupoInput.addEventListener('keyup', () => {
    const filter = buscarIndividuosGrupoInput.value.toUpperCase();
    for (let item of individualSelectionList.getElementsByClassName('individual-checkbox-item')) {
        const text = item.querySelector('label')?.textContent.toUpperCase() || '';
        item.style.display = text.includes(filter) ? "" : "none";
    }
});

buscarGruposIndividualInput.addEventListener('keyup', () => {
    const filter = buscarGruposIndividualInput.value.toUpperCase();
    for (let item of groupSelectionList.getElementsByClassName('group-radio-item')) {
        const text = item.querySelector('label')?.textContent.toUpperCase() || '';
        item.style.display = text.includes(filter) ? "" : "none";
    }
});

// =================== Inicialización ===================

const initialGrupos = ["Grupo A", "Grupo B", "Grupo C", "Grupo D"];
initialGrupos.forEach(grupo => {
    const btn = createListButton({
        text: grupo,
        iconClass: 'bi bi-tag-fill',
        classes: ['grupo-item']
    });
    listaGrupos.appendChild(btn);
});

const initialIndividuales = ["Persona 1", "Persona 2", "Persona 3", "Persona 4", "Persona 5"];
initialIndividuales.forEach(persona => {
    const btn = createListButton({
        text: persona,
        iconClass: 'bi bi-person-circle',
        classes: ['individual-item']
    });
    listaIndividuales.appendChild(btn);
});
