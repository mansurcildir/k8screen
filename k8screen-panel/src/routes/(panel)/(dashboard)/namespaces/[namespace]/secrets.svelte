<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Secret } from '$lib/model/Secret';
  import { secretAPI } from '$lib/service/secret-service';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import * as yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconEllipsis from 'lucide-svelte/icons/ellipsis';
  import Pagination from '$lib/components/pagination.svelte';
  import { secrets, getAllSecrets, loadingSecret } from '$lib/store';

  export let namespace;

  let perPage: number = 5;
  let details: string;
  let paginated: Secret[] = [];
  let k8sItem: string = '';
  let open: boolean;

  $: if (namespace) {
    getAllSecrets(namespace).then(() => load(1));
  }

  const load = (page: number) => {
    const startIndex = (page - 1) * perPage;
    const endIndex = page * perPage;
    paginated = $secrets.slice(startIndex, endIndex);
  };

  const getDetails = async (): Promise<string> => {
    open = true;
    details = await secretAPI.getSecretDetails(namespace, k8sItem);
    return details;
  };

  const updateItem = async () => {
    details = await secretAPI.updateSecret(namespace, k8sItem, yaml.parse(k8sItem));
    return details;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex flex-grow flex-col justify-between gap-8 overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>TYPE</Table.Head>
          <Table.Head>DATA</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>OPTIONS</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if $loadingSecret}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each paginated as secret}
            <Table.Row
              on:click={() => {
                k8sItem = secret.name;
                open = true;
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
                    <Button class="h-auto p-2" variant="ghost">
                      <IconEllipsis />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item
                        class="cursor-pointer"
                        onclick={() => {
                          k8sItem = secret.name;
                          getDetails();
                        }}
                        >View
                      </DropdownMenu.Item>
                      <DropdownMenu.Item class="cursor-pointer">Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <Pagination perPage={perPage} load={load} count={$secrets.length} />
  </div>

  {#if k8sItem}
    <Terminal
      type="secret"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      details={details}
      bind:open={open}
    />
  {/if}
</div>
