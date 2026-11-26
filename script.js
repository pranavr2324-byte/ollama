/**
 * Scholaris - Study Planner Dashboard
 * Main Application Logic (Theme Toggle Edition)
 */

document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const currentDateEl = document.getElementById('current-date');
    const totalTasksCountEl = document.getElementById('total-tasks-count');
    const completedTasksCountEl = document.getElementById('completed-tasks-count');
    const pendingTasksCountEl = document.getElementById('pending-tasks-count');
    const progressBarFillEl = document.getElementById('progress-bar-fill');
    const progressPercentageEl = document.getElementById('progress-percentage');
    const sidebarRateEl = document.getElementById('sidebar-rate');
    const sidebarSubjectsEl = document.getElementById('sidebar-subjects');

    const taskForm = document.getElementById('task-form');
    const taskSubjectInput = document.getElementById('task-subject');
    const taskNameInput = document.getElementById('task-name');
    const taskPrioritySelect = document.getElementById('task-priority');
    const taskDueInput = document.getElementById('task-due');

    const tasksContainer = document.getElementById('tasks-container');
    const emptyStateEl = document.getElementById('empty-state');
    const searchInput = document.getElementById('search-input');
    const filterTabs = document.querySelectorAll('.filter-tab');

    const clearCompletedBtn = document.getElementById('clear-completed-btn');
    const clearAllBtn = document.getElementById('clear-all-btn');
    const themeToggleBtn = document.getElementById('theme-toggle-btn');
    const themeIcon = document.getElementById('theme-icon');
    const themeText = document.getElementById('theme-text');
    const toastContainer = document.getElementById('toast-container');

    // State
    let tasks = JSON.parse(localStorage.getItem('scholaris_tasks')) || [
        {
            id: '1',
            subject: 'Mathematics',
            name: 'Multivariable Calculus Problem Set 3',
            priority: 'high',
            dueDate: '2026-12-01',
            completed: false,
            createdAt: Date.now() - 3600000
        },
        {
            id: '2',
            subject: 'Computer Science',
            name: 'Implement Red-Black Tree in C++',
            priority: 'urgent',
            dueDate: '2026-11-28',
            completed: true,
            createdAt: Date.now() - 7200000
        },
        {
            id: '3',
            subject: 'Physics',
            name: 'Review Quantum Mechanics Wavefunctions',
            priority: 'normal',
            dueDate: '2026-12-05',
            completed: false,
            createdAt: Date.now() - 10800000
        }
    ];

    let currentFilter = 'all';
    let searchQuery = '';

    // Initialize Theme
    const savedTheme = localStorage.getItem('scholaris_theme') || 'dark';
    setTheme(savedTheme);

    // Initialize UI
    initDate();
    renderTasks();

    // Event Listeners
    themeToggleBtn.addEventListener('click', toggleTheme);
    taskForm.addEventListener('submit', handleAddTask);
    searchInput.addEventListener('input', (e) => {
        searchQuery = e.target.value.trim().toLowerCase();
        renderTasks();
    });

    filterTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            filterTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            currentFilter = tab.dataset.filter;
            renderTasks();
        });
    });

    clearCompletedBtn.addEventListener('click', () => {
        const completedCount = tasks.filter(t => t.completed).length;
        if (completedCount === 0) {
            showToast('No completed tasks to clear.', 'info');
            return;
        }
        if (confirm(`Remove ${completedCount} completed task(s)?`)) {
            tasks = tasks.filter(t => !t.completed);
            saveAndRender();
            showToast('Cleared all completed tasks.', 'success');
        }
    });

    clearAllBtn.addEventListener('click', () => {
        if (tasks.length === 0) return;
        if (confirm('Are you sure you want to reset and delete all study tasks?')) {
            tasks = [];
            saveAndRender();
            showToast('All tasks have been reset.', 'info');
        }
    });

    // Theme Functions
    function setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('scholaris_theme', theme);
        if (theme === 'light') {
            themeText.textContent = 'Dark Mode';
            themeIcon.innerHTML = '<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>';
        } else {
            themeText.textContent = 'Light Mode';
            themeIcon.innerHTML = '<circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>';
        }
    }

    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        setTheme(newTheme);
        showToast(`Switched to ${newTheme === 'light' ? 'Light' : 'Dark'} Theme`, 'info');
    }

    // Functions
    function initDate() {
        const options = { weekday: 'long', year: 'numeric', month: 'short', day: 'numeric' };
        const today = new Date();
        currentDateEl.textContent = today.toLocaleDateString('en-US', options);
    }

    function handleAddTask(e) {
        e.preventDefault();

        const subject = taskSubjectInput.value.trim();
        const name = taskNameInput.value.trim();
        const priority = taskPrioritySelect.value;
        const dueDate = taskDueInput.value;

        if (!subject || !name) return;

        const newTask = {
            id: generateId(),
            subject,
            name,
            priority,
            dueDate,
            completed: false,
            createdAt: Date.now()
        };

        tasks.unshift(newTask);
        saveAndRender();

        // Reset form
        taskForm.reset();
        taskPrioritySelect.value = 'normal';
        taskSubjectInput.focus();

        showToast('Study task added successfully!', 'success');
    }

    function toggleTaskStatus(id) {
        tasks = tasks.map(task => {
            if (task.id === id) {
                const newStatus = !task.completed;
                if (newStatus) {
                    showToast(`Completed: "${task.name}"`, 'success');
                }
                return { ...task, completed: newStatus };
            }
            return task;
        });
        saveAndRender();
    }

    function deleteTask(id) {
        tasks = tasks.filter(task => task.id !== id);
        saveAndRender();
        showToast('Task deleted.', 'info');
    }

    function saveAndRender() {
        localStorage.setItem('scholaris_tasks', JSON.stringify(tasks));
        renderTasks();
    }

    function getFilteredTasks() {
        return tasks.filter(task => {
            const matchesFilter =
                currentFilter === 'all' ? true :
                currentFilter === 'active' ? !task.completed :
                currentFilter === 'completed' ? task.completed : true;

            const matchesSearch =
                task.name.toLowerCase().includes(searchQuery) ||
                task.subject.toLowerCase().includes(searchQuery);

            return matchesFilter && matchesSearch;
        });
    }

    function renderTasks() {
        const filteredTasks = getFilteredTasks();

        // Update Metrics
        const totalCount = tasks.length;
        const completedCount = tasks.filter(t => t.completed).length;
        const pendingCount = totalCount - completedCount;
        const progressPercent = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;
        const uniqueSubjects = new Set(tasks.map(t => t.subject.toLowerCase())).size;

        totalTasksCountEl.textContent = totalCount;
        completedTasksCountEl.textContent = completedCount;
        pendingTasksCountEl.textContent = pendingCount;

        progressBarFillEl.style.width = `${progressPercent}%`;
        progressPercentageEl.textContent = `${progressPercent}%`;
        sidebarRateEl.textContent = `${progressPercent}%`;
        sidebarSubjectsEl.textContent = uniqueSubjects;

        // Render task list
        tasksContainer.innerHTML = '';

        if (filteredTasks.length === 0) {
            emptyStateEl.classList.remove('hidden');
        } else {
            emptyStateEl.classList.add('hidden');

            filteredTasks.forEach(task => {
                const taskEl = document.createElement('div');
                taskEl.className = `task-item ${task.completed ? 'completed' : ''}`;

                const formattedDate = task.dueDate ? formatDate(task.dueDate) : '';

                taskEl.innerHTML = `
                    <div class="task-left">
                        <label class="task-checkbox-wrap" title="${task.completed ? 'Mark incomplete' : 'Mark complete'}">
                            <input type="checkbox" ${task.completed ? 'checked' : ''} data-id="${task.id}" class="task-checkbox">
                            <svg width="12" height="10" viewBox="0 0 12 10" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="1.5 6 4.5 9 10.5 1"></polyline></svg>
                        </label>
                        <div class="task-content">
                            <div class="task-meta-info">
                                <span class="task-subject-tag">${escapeHtml(task.subject)}</span>
                                ${task.priority !== 'normal' ? `<span class="task-priority-tag ${task.priority}">${task.priority}</span>` : ''}
                                ${formattedDate ? `<span class="task-due-date"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>${formattedDate}</span>` : ''}
                            </div>
                            <div class="task-title" title="${escapeHtml(task.name)}">${escapeHtml(task.name)}</div>
                        </div>
                    </div>
                    <button class="task-delete-btn" data-id="${task.id}" title="Delete task">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>
                    </button>
                `;

                // Event listener for checkbox
                const checkbox = taskEl.querySelector('.task-checkbox');
                checkbox.addEventListener('change', () => {
                    toggleTaskStatus(task.id);
                });

                // Event listener for delete button
                const deleteBtn = taskEl.querySelector('.task-delete-btn');
                deleteBtn.addEventListener('click', () => {
                    deleteTask(task.id);
                });

                tasksContainer.appendChild(taskEl);
            });
        }
    }

    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const iconSvg = type === 'success'
            ? '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="m9 11 3 3L22 4"/></svg>'
            : '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>';

        toast.innerHTML = `${iconSvg} <span>${escapeHtml(message)}</span>`;
        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'toastOut 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards';
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 3000);
    }

    function generateId() {
        return '_' + Math.random().toString(36).substr(2, 9);
    }

    function formatDate(dateString) {
        const [year, month, day] = dateString.split('-');
        const date = new Date(year, month - 1, day);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        div.innerHTML = div.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#039;');
        return div.innerHTML;
    }
});
