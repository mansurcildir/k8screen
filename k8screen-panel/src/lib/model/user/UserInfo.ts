import type { Role } from './Role';

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  picture: string;
  config: string;
  roles: Role[];
}
