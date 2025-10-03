import { Component } from '@angular/core';

interface Project {
  name: string;
  tasks: number;
  members: number;
  progress: number; // en pourcentage (0-100)
}

interface Task {
  name: string;
  dueDate: string;
  status: 'To do' | 'In progress' | 'Done';
}

interface Notification {
  message: string;
  date: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: true,
})
export class DashboardComponent {
  userName = 'John Doe';

  projects: Project[] = [
    { name: 'Project A', tasks: 8, members: 2, progress: 40 },
    { name: 'Project B', tasks: 15, members: 5, progress: 70 },
    { name: 'Website Redesign', tasks: 5, members: 3, progress: 90 }
  ];

  tasks: Task[] = [
    { name: 'Fix login bug', dueDate: '2025-08-02', status: 'In progress' },
    { name: 'Add task filters', dueDate: '2025-08-03', status: 'To do' },
    { name: 'Release MVP', dueDate: '2025-08-05', status: 'Done' }
  ];

  notifications: Notification[] = [
    { message: 'Task "Design" updated by Alice.', date: '2025-07-30' },
    { message: 'New project created: "Website".', date: '2025-07-29' },
    { message: 'User Bob joined the team.', date: '2025-07-28' }
  ];
}
