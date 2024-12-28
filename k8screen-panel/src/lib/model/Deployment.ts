export interface Deployment {
  name: string;
  total_replicas: number;
  ready_replicas: number;
  up_to_date: number;
  available: number;
  age: string;
}
