import type { Role } from './Role';

export interface UserItem {
  id: string;
  username: string;
  email: string;
  picture: string;
  config: string;
  roles: Role[];
}
