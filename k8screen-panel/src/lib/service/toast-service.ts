import { writable } from 'svelte/store';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error';
}

export const toasts = writable<Toast[]>([]);

let nextId = 1;

export const toastService = {
  show(message: string, type: 'success' | 'error' = 'success', duration = 3000) {
    const id = nextId++;
    const toast: Toast = { id, message, type };

    toasts.update((all) => {
      const updated = [...all, toast];
      return updated.length > 3 ? updated.slice(-3) : updated;
    });

    setTimeout(() => {
      this.remove(id);
    }, duration);
  },

  remove(id: number) {
    toasts.update((all) => all.filter((toast) => toast.id !== id));
  }
};
