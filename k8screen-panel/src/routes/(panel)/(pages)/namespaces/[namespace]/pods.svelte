<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Pod } from '$lib/model/Pod';
  import { podAPI } from '$lib/service/pod-service';
  import Button from '$lib/components/ui/button/button.svelte';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import Terminal from '$lib/components/terminal.svelte';
  import * as yaml from 'yaml';

  export let namespace;
  let loading = false;
  let loadingTable = false;
  let option: string = 'DETAILS';
  let details: string;
  let logs: string;
  let execRes: string;

  let pods: Pod[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllPods();
  }

  const handlePod = (pod: string) => {
    k8sItem = pod;
    open = true;
    getDetails();
    getLogs();
  };

  const getAllPods = async () => {
    try {
      loadingTable = true;
      pods = await podAPI.getAllPods(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const getLogs = async (): Promise<string> => {
    loading = true;
    logs = await podAPI.getPodLogs(namespace, k8sItem);
    loading = false;
    return logs;
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await podAPI.getPodDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (editedPod: string) => {
    try {
      loading = true;
      return podAPI.updatePod(namespace, k8sItem, yaml.parse(editedPod));
    } finally {
      loading = false;
    }
  };

  const exec = async (execReq: string): Promise<string> => {
    loading = true;
    execRes = await podAPI.exec(namespace, k8sItem, ['sh', '-c', execReq]);
    loading = false;
    return execRes;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>STATUS</Table.Head>
          <Table.Head>RESTARTS</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head></Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loadingTable}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each pods as pod}
            <Table.Row
              on:click={() => {
                handlePod(pod.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{pod.name}</Table.Cell>
              <Table.Cell>{pod.ready_containers}</Table.Cell>
              <Table.Cell>{pod.status}</Table.Cell>
              <Table.Cell>{pod.restarts}</Table.Cell>
              <Table.Cell>{pod.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="p-2 h-auto" variant="ghost">
                      <svg
                        width="24"
                        height="24"
                        class="h-4 w-4"
                        role="img"
                        aria-label="dots horizontal,"
                        viewBox="0 0 15 15"
                        fill="currentColor"
                        xmlns="http://www.w3.org/2000/svg"
                        ><path
                          fill-rule="evenodd"
                          clip-rule="evenodd"
                          d="M3.625 7.5C3.625 8.12132 3.12132 8.625 2.5 8.625C1.87868 8.625 1.375 8.12132 1.375 7.5C1.375 6.87868 1.87868 6.375 2.5 6.375C3.12132 6.375 3.625 6.87868 3.625 7.5ZM8.625 7.5C8.625 8.12132 8.12132 8.625 7.5 8.625C6.87868 8.625 6.375 8.12132 6.375 7.5C6.375 6.87868 6.87868 6.375 7.5 6.375C8.12132 6.375 8.625 6.87868 8.625 7.5ZM12.5 8.625C13.1213 8.625 13.625 8.12132 13.625 7.5C13.625 6.87868 13.1213 6.375 12.5 6.375C11.8787 6.375 11.375 6.87868 11.375 7.5C11.375 8.12132 11.8787 8.625 12.5 8.625Z"
                          fill="currentColor"
                        ></path></svg
                      >
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item>View</DropdownMenu.Item>
                      <DropdownMenu.Item>Edit</DropdownMenu.Item>
                      <DropdownMenu.Item>Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
  </div>

  <Terminal
    k8sItem={k8sItem}
    option={option}
    details={details}
    logs={logs}
    loading={loading}
    open={open}
    isPod={true}
    getDetails={getDetails}
    updateItem={updateItem}
    getLogs={getLogs}
    exec={exec}
  />
</div>
