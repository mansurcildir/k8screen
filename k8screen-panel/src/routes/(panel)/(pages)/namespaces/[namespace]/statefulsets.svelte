<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';

  import * as Table from '$lib/components/ui/table';
  import type { StatefulSet } from '$lib/model/StatefulSet';
  import { statefulSetAPI } from '$lib/service/statefulset-service';
  import * as yaml from 'yaml';

  export let namespace;
  let loading = true;
  let loadingTable = false;
  let option: string = 'DETAILS';
  let details: string;

  let statefulSets: StatefulSet[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllStatefulSets();
  }

  const handleStatefulSet = (service: string) => {
    k8sItem = service;
    open = true;
    getDetails();
  };

  const getAllStatefulSets = async () => {
    try {
      loadingTable = true;
      statefulSets = await statefulSetAPI.getAllStatefulSets(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await statefulSetAPI.getStatefulSetDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (editedStatefulSet: string) => {
    try {
      loading = true;
      return statefulSetAPI.updateStatefulSet(namespace, k8sItem, yaml.parse(editedStatefulSet));
    } finally {
      loading = false;
    }
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>AGE</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loadingTable}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each statefulSets as statefulSet}
            <Table.Row
              on:click={() => {
                handleStatefulSet(statefulSet.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{statefulSet.name}</Table.Cell>
              <Table.Cell>{statefulSet.ready_replicas}/{statefulSet.total_replicas}</Table.Cell>
              <Table.Cell>{statefulSet.age}</Table.Cell>
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
    loading={loading}
    open={open}
    getDetails={getDetails}
    updateItem={updateItem}
  />
</div>
