<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import { OptionTerminal } from '$lib/model/enum';
  import type { Secret } from '$lib/model/Secret';
  import { secretAPI } from '$lib/service/secret-service';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import * as yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import { onMount } from 'svelte';

  export let namespace;
  let loading = false;
  let loadingTable = false;
  let option: OptionTerminal;
  let details: string;

  let secrets: Secret[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllSecrets();
  }

  const load = (secret: string) => {
    k8sItem = secret;
    open = true;
    option = OptionTerminal.DETAIL;
    getDetails();
  };

  const getAllSecrets = async () => {
    try {
      loadingTable = true;
      secrets = await secretAPI.getAllSecrets(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const updateItem = async (editedSecret: string) => {
    try {
      loading = true;
      return secretAPI.updateSecret(namespace, k8sItem, yaml.parse(editedSecret));
    } finally {
      loading = false;
    }
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await secretAPI.getSecretDetails(namespace, k8sItem);
    loading = false;
    return details;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>TYPE</Table.Head>
          <Table.Head>DATA</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>Options</Table.Head>
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
          </Table.Row>
        {:else}
          {#each secrets as secret}
            <Table.Row
              on:click={() => {
                load(secret.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{secret.name}</Table.Cell>
              <Table.Cell>{secret.type}</Table.Cell>
              <Table.Cell>{secret.data_size}</Table.Cell>
              <Table.Cell>{secret.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="p-2 h-auto" variant="ghost">
                      <IconKebabMenu />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item onclick={() => option = OptionTerminal.DETAIL}>View</DropdownMenu.Item>
                      <DropdownMenu.Item onclick={() => option = OptionTerminal.EDIT}>Edit</DropdownMenu.Item>
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
    type='secret'
    {getDetails}
    {updateItem}
    {k8sItem}
    {option}
    {details}
    {loading}
    {open}
  />
</div>
